import Joi from "joi";

export const schemas = {
    task: Joi.object({
        task_id: Joi.number().integer().required(),
        download_directory: Joi.string().required(),
        markus_URL: Joi.string().uri().required(),
        assignment_id: Joi.number().integer().required(),
        api_key: Joi.string().required()
    })
}
