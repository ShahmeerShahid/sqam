import React from "react";
import { ThemeProvider, Box, Heading, Stack, Text, Flex, Icon} from "@chakra-ui/core";
import { withSnackbar } from "notistack";
import { useForm } from "react-hook-form";
import { useHistory } from "react-router-dom";
import { createTask } from "../../requests/tasks";
import TaskRow from "../TaskRow";

function TaskDetail({
  tid,
}) {
  return (
    <div>
            <Box
                backgroundColor="white"
                shadow="sm"
                borderRadius="lg"
                pl={3}
                pr={3}
                pt={5}
                pb={5}
            >
                <Flex
                    display="flex"
                    flexDirection="row"
                    alignItems="center"
                    justifyContent="center"
                    pb={2}
                >
                    <Icon name="chevron-left" />
                    <Heading
                        size="md"
                        as="h2"
                        lineHeight="shorter"
                        fontWeight="bold"
                        alignItems="center"
                        fontFamily="heading"
                    >
                        Details for {tid}
                    </Heading>
                    <Icon name="chevron-right" />
                </Flex>
                <Stack ml={4} spacing={2} shouldWrapChildren mt={4} mr={4}>
                    <TaskRow Field_Name="tid" Field_Value={tid} />
                    <TaskRow Field_Name="Assignment name" Field_Value="CSC343-A2" />
                    <TaskRow Field_Name="create_tables" Field_Value="CSC343-A2" />
                    <TaskRow Field_Name="create_trigger" Field_Value="CSC343-A2" />
                    <TaskRow Field_Name="create_function" Field_Value="CSC343-A2" />
                    <TaskRow Field_Name="load_data" Field_Value="CSC343-A2" />
                    <TaskRow Field_Name="solutions" Field_Value="CSC343-A2" />
                    <TaskRow Field_Name="submissions" Field_Value="CSC343-A2" />
                    <TaskRow Field_Name="submission_file_name" Field_Value={tid} />
                    <TaskRow Field_Name="timeout" Field_Value={tid} />
                    <TaskRow Field_Name="max_marks" Field_Value={tid} />
                    <TaskRow Field_Name="max_marks_per_question" Field_Value={tid} />
                    <TaskRow Field_Name="question_names" Field_Value={tid} />
                    <TaskRow Field_Name="db_type" Field_Value={tid} />
                    <TaskRow Field_Name="marking_type" Field_Value={tid} />



                </Stack>
            </Box>

    </div>
  );
}

export default withSnackbar(TaskDetail);
