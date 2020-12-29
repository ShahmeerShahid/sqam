import React, { useState } from "react";
import { Box, Heading, Stack, Icon, Flex, Select } from "@chakra-ui/core";
import { withSnackbar } from "notistack";

function LogRow({ logField }) {
  const strFinal = ''.concat('> ', logField['source'], ' -- [',logField['timestamp'], "]", logField['text'], "\n" );
  return (
      <Box d="flex" justifyContent="space-between" alignItems="center">
        <pre>{strFinal}</pre>
      </Box>
  );
}

function TaskLog({ taskLogs, tid }) {
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
                {taskLogs.length !== 0  ? (
                  taskLogs.map((key, index) => (
                    ( (value === key["source"] || value === "All") && <LogRow key={index} logField={key} /> )
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
