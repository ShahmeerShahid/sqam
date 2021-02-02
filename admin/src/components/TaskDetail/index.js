import React from "react";
import {
  Box,
  Heading,
  Stack,
  Flex,
  Icon,
  ThemeProvider,
  Text,
} from "@chakra-ui/core";
import { withSnackbar } from "notistack";

function TaskRow({ fieldName, fieldValue }) {
  const stripped = fieldValue.replace(/,/g, ", ");
  return (
    <div>
      <ThemeProvider>
        <Flex
          bg="darkturquoise"
          w="100%"
          p={3}
          px={5}
          minHeight="50px"
          py={4}
          borderRadius="lg"
          justifyContent="space-between"
          alignItems="center"
        >
          <Flex flexDirection="row" justifyContent="center" alignItems="center">
            <Text>{fieldName}: </Text>
          </Flex>
          <Box>
            <Text> {stripped} </Text>
          </Box>
        </Flex>
      </ThemeProvider>
    </div>
  );
}

function TaskDetail({ taskData, tid }) {
  if (taskData) {
    ["_id", "logs", "createdAt", "updatedAt", "__v"].forEach(
      (e) => delete taskData[e]
    );
  }
  return (
    <div>
      <Box
        backgroundColor="gainsboro"
        shadow="sm"
        borderRadius="lg"
        maxH={1000}
        overflowY="scroll"
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
          {taskData ? (
            Object.keys(taskData).map((key, index) =>
              String(taskData[key]) ? (
                typeof taskData[key] == "object" &&
                !Array.isArray(taskData[key]) ? (
                  Object.keys(taskData[key]).map((key2, index2) => (
                    <TaskRow
                      key={index2}
                      fieldName={key2}
                      fieldValue={String(taskData[key][key2])}
                    />
                  ))
                ) : (
                  <TaskRow
                    key={index}
                    fieldName={key}
                    fieldValue={String(taskData[key])}
                  />
                )
              ) : null
            )
          ) : (
            <h1>No tasks to display</h1>
          )}
        </Stack>
      </Box>
    </div>
  );
}

export default withSnackbar(TaskDetail);
