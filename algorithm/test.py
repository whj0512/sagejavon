import os

llm_name = os.getenv('LLM_NAME')
zhipuai_api_key = os.getenv('ZHIPUAI_API_KEY')
glm_model_name = os.getenv('GLM_MODEL_NAME')

print(llm_name, zhipuai_api_key, glm_model_name)
