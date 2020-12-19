import React, { useState } from "react";
import { Box, Heading, Stack, Icon, Flex, Select } from "@chakra-ui/core";
import { withSnackbar } from "notistack";
import Terminal from "./Terminal";

function LogRow({ Log_Field }) {
  return (

      <Box d="flex" justifyContent="space-between" alignItems="center">
        <Terminal>
          {async ({ print, println }) => {
            await println(Log_Field["text"], Log_Field["source"],Log_Field["timestamp"], 100);
          }}
        </Terminal>
      </Box>

  );
}

function TaskLog({ tid, TaskLogs }) {
  let logSources = ["All", "frontend", "automarker", "connector", "api"];
  const [value, setValue] = useState("All")
  const handleChange = (event) => {
    setValue(event.target.value)
  }

  return (
    <div>
      <Box>
        <Box
          backgroundColor="gainsboro"
          borderRadius="lg"
          shadow="sm"
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
            justifyContent="space-between"
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
              Logs for {tid} 
              
            </Heading>
            <Icon name="chevron-right" />

            <Select 
              variant="filled" 
              value={value}
              onChange={handleChange} 
              w="40%"
            >
                {logSources.map((key, index) => (
                  <option key={index} value={key}>{key}</option>
                ))}
            </Select>
          </Flex>
          <Stack shouldWrapChildren spacing={4} ml={4} mt={4}>
            <Stack shouldWrapChildren spacing={2}>
              <Box
                backgroundColor="black"
                color="white"
                shadow="sm"
                maxH={1000}
                overflowY="scroll"
                pl={3}
                pr={3}
                pt={5}
                pb={5}
              >
                {TaskLogs.length !== 0  ? (
                  TaskLogs.map((key, index) => (
                    ( (value === key["source"] || value === "All") && <LogRow key={index} Log_Field={key} /> )
                  ))
                ) : (
                  <h1>No tasks to display</h1>
                )}
              </Box>
            </Stack>
          </Stack>
        </Box>
      </Box>
    </div>
  );
}

export default withSnackbar(TaskLog);
