const assert = require('node:assert/strict')
const fs = require('node:fs')
const path = require('node:path')
const { test } = require('node:test')
const ts = require('typescript')
const { parse, compileScript } = require('@vue/compiler-sfc')
const { ref, computed, reactive } = require('vue')

const source = fs.readFileSync(
  path.join(__dirname, '../src/views/program-detail/index.vue'),
  'utf8',
)
const { descriptor } = parse(source)
const script = ts.transpileModule(
  descriptor.scriptSetup.content.replace(/^import .*\n/gm, ''),
  {
    compilerOptions: {
      target: ts.ScriptTarget.ES2022,
      module: ts.ModuleKind.CommonJS,
    },
  },
).outputText
const success = (data) => ({ data: { code: 'SUCCESS', data } })

function setup(overrides = {}, id = '113') {
  const calls = [],
    notices = []
  const route = reactive({ query: { id } })
  const dependencies = {
    exports: {},
    ref,
    computed,
    watch() {},
    useRoute: () => route,
    useMessage: () => ({
      warning: (text) => notices.push(text),
      error: (text) => notices.push(text),
    }),
    localStorage: { getItem: () => null },
    programDetails: async (id) => {
      calls.push(['detail', id])
      return success({ id, questionText: 'test', knowledgeConcept: [] })
    },
    recordList: async (id) => {
      calls.push(['records', id])
      return success([])
    },
    questionCode: async (request) => {
      calls.push(['submit', request])
      return success({ score: 80, suggestion: 'test', correctAnswer: null })
    },
    reviewQuestion: async () => success(1),
    ...overrides,
  }
  const state = new Function(
    ...Object.keys(dependencies),
    `${script}; return {
    loadQuestion, submitCode, submitReview, code, records, programDetail, isLoading,
    isPageLoading, submitNum, showModal, correctAnswer, loadError, isLiked, isReviewing
  }`,
  )(...Object.values(dependencies))
  return { ...state, calls, notices, route }
}

test('template compiles with submit and load handlers bound', () => {
  const result = compileScript(descriptor, {
    id: 'program-test',
    inlineTemplate: true,
  })
  assert(result.bindings.submitCode)
  assert(!result.content.includes('_ctx.editorMounted'))
})

test('initial load requests detail and records once using numeric question ID', async () => {
  const state = setup()
  await state.loadQuestion()
  assert.deepEqual(state.calls, [
    ['detail', 113],
    ['records', 113],
  ])
  assert.equal(state.isPageLoading.value, false)
})

test('invalid IDs make no API calls and block submission', async () => {
  for (const id of [undefined, '', 'abc', '0', '-1', ['113', '114']]) {
    const state = setup({}, id)
    state.route.query.id = id
    await state.loadQuestion()
    await state.submitCode()
    assert.equal(state.calls.length, 0)
    assert.match(state.loadError.value, /编号无效/)
  }
})

test('empty code is rejected before scoring', async () => {
  const state = setup()
  await state.loadQuestion()
  state.code.value = '   '
  await state.submitCode()
  assert(!state.calls.some(([type]) => type === 'submit'))
})

test('business, malformed result and network failures release loading without counting a submission', async () => {
  for (const response of [
    { data: { code: 'LLM_ERROR', data: null } },
    { data: { code: 'ILLEGAL_ARGUMENT', message: '非法参数', data: null } },
    success(null),
    success({ score: null }),
    new Error('network unavailable'),
  ]) {
    const state = setup({
      questionCode: async () => {
        if (response instanceof Error) throw response
        return response
      },
    })
    await state.loadQuestion()
    state.code.value = 'class Main {}'
    await state.submitCode()
    assert.equal(state.isLoading.value, false)
    assert.equal(state.submitNum.value, 0)
    assert.equal(state.showModal.value, false)
    assert.equal(state.notices.length, 1)
  }
})

test('success increments persisted history count, accepts null answer and refreshes history', async () => {
  let reads = 0,
    submitted
  const state = setup({
    recordList: async () =>
      success(
        Array.from({ length: ++reads === 1 ? 3 : 4 }, (_, i) => ({
          recordId: i,
        })),
      ),
    questionCode: async (request) => {
      submitted = request
      return success({ score: 90, suggestion: 'ok', correctAnswer: null })
    },
  })
  await state.loadQuestion()
  state.code.value = 'class Main {}'
  await state.submitCode()
  assert.equal(submitted.submitNum, 4)
  assert.equal(state.records.value.length, 4)
  assert.equal(state.submitNum.value, 4)
  assert.equal(state.correctAnswer.value, null)
  assert.equal(state.showModal.value, true)
  assert.equal(state.isLoading.value, false)
})

test('failed submit can be retried and does not advance count', async () => {
  let attempts = 0
  const counts = []
  const state = setup({
    questionCode: async (request) => {
      counts.push(request.submitNum)
      return ++attempts === 1
        ? { data: { code: 'LLM_ERROR', data: null } }
        : success({ score: 80 })
    },
  })
  await state.loadQuestion()
  state.code.value = 'class Main {}'
  await state.submitCode()
  await state.submitCode()
  assert.deepEqual(counts, [1, 1])
  assert.equal(state.showModal.value, true)
})

test('duplicate clicks only send one scoring request', async () => {
  let finish,
    count = 0
  const state = setup({
    questionCode: () => {
      count++
      return new Promise((resolve) => {
        finish = resolve
      })
    },
  })
  await state.loadQuestion()
  state.code.value = 'class Main {}'
  const pending = state.submitCode()
  await state.submitCode()
  assert.equal(count, 1)
  finish(success({ score: 70 }))
  await pending
  assert.equal(state.isLoading.value, false)
})

test('late detail response cannot overwrite a newly selected question', async () => {
  let finish
  const state = setup({
    programDetails: (id) =>
      id === 113
        ? new Promise((resolve) => {
            finish = resolve
          })
        : Promise.resolve(success({ id })),
  })
  const oldLoad = state.loadQuestion()
  state.route.query.id = '114'
  await state.loadQuestion()
  finish(success({ id: 113 }))
  await oldLoad
  assert.equal(state.programDetail.value.id, 114)
  assert.equal(state.isPageLoading.value, false)
})

test('null detail renders a missing question state', async () => {
  const state = setup({ programDetails: async () => success(null) })
  await state.loadQuestion()
  assert.equal(state.programDetail.value, null)
  assert.equal(state.loadError.value, '题目不存在')
  assert.equal(state.isPageLoading.value, false)
})

test('history refresh failure does not discard a successful score', async () => {
  let reads = 0
  const state = setup({
    recordList: async () => {
      if (++reads > 1) throw new Error('network unavailable')
      return success([])
    },
  })
  await state.loadQuestion()
  state.code.value = 'class Main {}'
  await state.submitCode()
  assert.equal(state.showModal.value, true)
  assert.equal(state.submitNum.value, 1)
  assert.equal(state.isLoading.value, false)
  assert.match(state.notices[0], /提交成功/)
})

test('failed initial history is retried before submitting', async () => {
  let reads = 0,
    submitted
  const state = setup({
    recordList: async () => {
      if (++reads === 1) throw new Error('network unavailable')
      return success([{ recordId: 1 }, { recordId: 2 }])
    },
    questionCode: async (request) => {
      submitted = request
      return success({ score: 80 })
    },
  })
  await state.loadQuestion()
  state.code.value = 'class Main {}'
  await state.submitCode()
  assert.equal(submitted.submitNum, 3)
  assert.equal(state.submitNum.value, 3)
})

test('late submission result does not open a modal on another question', async () => {
  let finish
  const state = setup({
    questionCode: () =>
      new Promise((resolve) => {
        finish = resolve
      }),
  })
  await state.loadQuestion()
  state.code.value = 'class Main {}'
  const pending = state.submitCode()
  state.route.query.id = '114'
  await state.loadQuestion()
  finish(success({ score: 80 }))
  await pending
  assert.equal(state.showModal.value, false)
  assert.equal(state.submitNum.value, 0)
  assert.equal(state.programDetail.value.id, 114)
})

test('failed review preserves reaction state and releases busy flag', async () => {
  const state = setup({
    reviewQuestion: async () => ({
      data: { code: 'JWT_ERROR', message: '登录失效' },
    }),
  })
  await state.loadQuestion()
  await state.submitReview(1)
  assert.equal(state.isLiked.value, false)
  assert.equal(state.isReviewing.value, false)
  assert.equal(state.notices[0], '登录失效')
})
