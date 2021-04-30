import mongoose from "mongoose";
import autoIncrement from "mongoose-auto-increment";
import constants from "../constants";
import { createSchema, ExtractDoc, Type, typedModel } from "ts-mongoose";

const SubmissionSchema = createSchema({
  name: Type.string({
    required: true,
  }),
  status: Type.string({
    enum: constants.statuses,
    required: true,
    default: "Pending",
  }),
});

export const LogSchema = createSchema({
  timestamp: Type.date({
    required: true,
  }),
  text: Type.string({
    required: true,
  }),
  source: Type.string({
    required: true,
    enum: constants.logSources,
  }),
});

export const TaskSchema = createSchema(
  {
    tid: Type.number({
      unique: true,
      default: 1,
      required: true,
    }),
    name: Type.string({
      required: true,
    }),
    status: Type.string({
      enum: constants.statuses,
      required: true,
      default: "Pending",
    }),
    connector: Type.string({
      required: true,
    }),
    submissions: Type.array().of(Type.schema().of(SubmissionSchema)),
    num_submissions: Type.number({
      default: 0,
      required: true,
    }),
    max_marks: Type.number({
      default: 0,
      required: true,
    }),
    max_marks_per_question: Type.array({ required: true, default: [] }).of(
      Type.number()
    ),
    marking_type: Type.string({
      default: "partial",
      required: true,
      enum: constants.markingTypes,
    }),
    question_names: Type.array({
      required: true,
      default: [],
    }).of(Type.string()),
    submission_file_name: Type.string({
      default: "",
      required: true,
    }),
    initFile: Type.string({
      default: "",
    }),
    solutions: Type.string({
      default: "",
    }),
    timeout: Type.number({
      type: Number,
      default: 100,
      max: 300,
      required: true,
    }),
    db_type: Type.string({
      default: "mysql",
      required: true,
      enum: constants.dbTypes,
    }),
    extra_fields: Type.mixed({
      required: false,
    }),
    logs: Type.array().of(Type.schema().of(LogSchema)), // Mongoose automatically sets default to []
  },
  {
    timestamps: true,
  }
);

// export function addAutoincrementPlugin () {
autoIncrement.initialize(mongoose.connection);
TaskSchema.plugin(autoIncrement.plugin, { model: "Task", field: "tid" });
// }

export const Task = typedModel("Task", TaskSchema);
export const Log = typedModel("Log", LogSchema);

export type TaskDoc = ExtractDoc<typeof TaskSchema>;
