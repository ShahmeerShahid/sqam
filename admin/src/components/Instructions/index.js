import { Heading, Stack, Link, List, ListItem } from "@chakra-ui/core";
import { Link as RouterLink } from "react-router-dom";

import React from "react";

// Instruction on how to load the Task information to start marking job
const Instructions = () => {
  return (
    <Stack p={5} shadow="md" borderWidth="1px" m={12}>
      <Heading as="h3" size="lg">
        How to use the Automarker
      </Heading>
      <List as="ol" styleType="decimal">
        <ListItem>
          Go to the{" "}
          <Link as={RouterLink} to="/tasks">
            tasks
          </Link>{" "}
          page
        </ListItem>
        <ListItem>Click Add Task</ListItem>
        <ListItem>
          Select your connector (i.e. the source website for student
          submissions)
        </ListItem>
        <ListItem>Enter the details for the marking task</ListItem>
        <ListItem>Drag and drop your init and solution SQL files</ListItem>
        <ListItem>Submit and wait for a bit (around 30 seconds)</ListItem>
        <ListItem>
          Go back to the tasks page and click on the newly created task
        </ListItem>
        <ListItem>
          Once completed, the status will be updated and you can download the
          report
        </ListItem>
      </List>
    </Stack>
  );
};

export default Instructions;
