const assert = require('node:assert/strict')
const fs = require('node:fs')
const path = require('node:path')
const { test } = require('node:test')
const ts = require('typescript')
const { parse, compileScript } = require('@vue/compiler-sfc')

const source = fs.readFileSync(
  path.join(__dirname, '../src/views/chat/index.vue'), 'utf8',
)
const script = source.slice(
  source.indexOf('async function onConversation()'),
  source.indexOf('\nfunction handleExport()'),
)
const code = ts.transpile(script, { target: ts.ScriptTarget.ES2022 })

function setup(messages, fail = false) {
  const saved = [], requests = [], warnings = []
  const loading = { value: false }, prompt = { value: 'unsent draft' }
  let finish
  const pending = new Promise(resolve => { finish = resolve })
  const dependencies = {
    prompt, loading,
    localStorage: { getItem: () => '1' },
    chatMessage: async data => { saved.push(data); return { status: 200 } },
    addChat: (_, data) => messages.push(data),
    updateChat: (_, index, data) => { messages[index] = data },
    updateChatSome: (_, index, data) => Object.assign(messages[index], data),
    getChatByUuidAndIndex: (_, index) => messages[index],
    dataSources: { value: messages },
    scrollToBottom: () => {},
    ms: { warning: message => warnings.push(message) },
    rag_url: '',
    updateKnowledgeNodes: async () => {},
    fetch: async (_, options) => {
      requests.push(JSON.parse(options.body))
      await pending
      if (fail) return { ok: false, status: 502, json: async () => ({ message: 'model unavailable' }) }
      return new Response(new ReadableStream({ start(stream) {
        stream.enqueue(new TextEncoder().encode('new answer'))
        stream.close()
      } }))
    },
    console: { log() {}, error() {} },
  }
  const functions = new Function(...Object.keys(dependencies),
    `let controller; ${code}; return { onConversation, onRegenerate }`,
  )(...Object.values(dependencies))
  return { ...functions, messages, saved, requests, warnings, loading, prompt, finish }
}

test('template binds the regenerate handler from script setup', () => {
  const { descriptor } = parse(source)
  const result = compileScript(descriptor, { id: 'chat-test', inlineTemplate: true })
  assert.equal(result.bindings.onRegenerate, 'setup-const')
  assert(!result.content.includes('_ctx.onRegenerate'))
})

test('regenerate replaces selected answer, preserves later messages and draft', async () => {
  const later = { text: 'later answer' }
  const state = setup([
    { inversion: true, text: 'original question' },
    { text: 'old answer', requestOptions: { prompt: 'original question' } },
    later,
  ])
  const request = state.onRegenerate(1)
  assert.equal(state.loading.value, true)
  assert.equal(state.messages[1].loading, true)
  await state.onRegenerate(1)
  assert.equal(state.requests.length, 1)
  state.finish()
  await request
  assert.equal(state.requests[0].query, 'original question')
  assert.equal(state.messages.length, 3)
  assert.equal(state.messages[1].text, 'new answer')
  assert.equal(state.messages[2], later)
  assert.equal(state.prompt.value, 'unsent draft')
  assert.deepEqual(state.saved, [{ chatId: 1, role: 1, content: 'new answer' }])
  assert.equal(state.loading.value, false)
})

test('legacy failed answer recovers preceding user query', async () => {
  const state = setup([{ inversion: true, text: 'legacy question' }, { text: 'SageJavon is thinking....' }])
  state.finish()
  await state.onRegenerate(1)
  assert.equal(state.requests[0].query, 'legacy question')
  assert.equal(state.messages[1].text, 'new answer')
})

test('missing question and invalid targets do not send requests', async () => {
  const state = setup([{ text: 'orphan answer' }, { inversion: true, text: 'question' }])
  await state.onRegenerate(0)
  await state.onRegenerate(1)
  await state.onRegenerate(99)
  assert.equal(state.warnings.length, 1)
  assert.equal(state.requests.length, 0)
})

test('failed regeneration clears loading and remains retryable', async () => {
  const state = setup([{ requestOptions: { prompt: 'question' } }], true)
  state.finish()
  await state.onRegenerate(0)
  assert.equal(state.messages[0].error, true)
  assert.equal(state.messages[0].loading, false)
  assert.equal(state.loading.value, false)
  assert.equal(state.saved.length, 0)
  await state.onRegenerate(0)
  assert.equal(state.requests.length, 2)
  assert.equal(state.requests[1].query, 'question')
})

test('ordinary submission still adds a question and a complete answer', async () => {
  const state = setup([])
  state.finish()
  await state.onConversation()
  assert.equal(state.messages.length, 2)
  assert.equal(state.messages[0].text, 'unsent draft')
  assert.equal(state.messages[1].text, 'new answer')
  assert.equal(state.prompt.value, '')
  assert.deepEqual(state.saved.map(message => message.role), [0, 1])
})
