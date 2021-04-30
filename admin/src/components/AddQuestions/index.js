import React from "react";
import {
  Button,
  Flex,
  Input,
  NumberInput,
  NumberInputField,
  NumberInputStepper,
  NumberIncrementStepper,
  NumberDecrementStepper,
} from "@chakra-ui/core";
import { DeleteIcon } from "@chakra-ui/icons";

// Single row of Question input containing question name and max mark and delete symbol 
function QuestionInput({
  index,
  question,
  handleNameChange,
  handleNumChange,
  handleDelete,
}) {
  return (
    <Flex
      display="flex"
      flexDirection="row"
      alignItems="center"
      justifyContent="center"
      pb={2}
      key={index}
    >
      <Input
        m={2}
        aria-label={`question-${index}-input`}
        name={`question_names,${index}`}
        placeholder="Question Name"
        value={question[0]}
        onChange={(e) => {
          handleNameChange(index, e.target.value);
        }}
        key={`input-${index}`}
        width="40%"
      />
      <NumberInput
        m={2}
        min={0}
        name={`max_marks_per_question,${index}`}
        defaultValue={0}
        value={parseInt(question[1])}
        width="40%"
        onChange={(value) => {
          handleNumChange(index, parseInt(value));
        }}
        key={`numinput-${index}`}
      >
        <NumberInputField aria-label={`num-${index}-input`} />
        <NumberInputStepper>
          <NumberIncrementStepper />
          <NumberDecrementStepper />
        </NumberInputStepper>
      </NumberInput>
      <Button
        aria-label="Delete question"
        data-testid={`delete-btn-${index}`}
        onClick={() => {
          handleDelete(index);
        }}
      >
        <DeleteIcon />
      </Button>
    </Flex>
  );
}

// Add Question mechanism for stage 2 of add task form -> to configure number of questions
function AddQuestions({ values, setFieldValue }) {
  let question_names = values.question_names;
  let max_marks_per_question = values.max_marks_per_question;

  const handleAddition = () => {
    question_names.push(`Q${question_names.length + 1}`);
    max_marks_per_question.push(0);
    setFieldValue("question_names", question_names);
    setFieldValue("max_marks_per_question", max_marks_per_question);
  };

  const handleNameChange = (index, value) => {
    if (index >= 0 && index < question_names.length) {
      question_names[index] = value;
      setFieldValue("question_names", question_names);
    }
  };

  const handleNumChange = (index, value) => {
    if (index >= 0 && index < max_marks_per_question.length) {
      max_marks_per_question[index] = value;
      setFieldValue("max_marks_per_question", max_marks_per_question);
    }
  };

  const handleDelete = (index) => {
    if (index >= 0 && index < question_names.length) {
      question_names.splice(index, 1);
      max_marks_per_question.splice(index, 1);
      setFieldValue("question_names", question_names);
      setFieldValue("max_marks_per_question", max_marks_per_question);
    }
  };

  let i = 0;
  let questions = [];
  for (i = 0; i < question_names.length; i++) {
    questions.push([question_names[i], max_marks_per_question[i]]);
  }

  return (
    <>
      <Button onClick={handleAddition} mb={2}>
        Add Question
      </Button>
      {questions.length > 0 &&
        questions.map((question, index) => {
          return (
            <QuestionInput
              key={index}
              index={index}
              question={question}
              handleNameChange={handleNameChange}
              handleNumChange={handleNumChange}
              handleDelete={handleDelete}
            />
          );
        })}
    </>
  );
}

export default AddQuestions;
