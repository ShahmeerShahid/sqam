import React from "react";
import { Box, Heading, Stack, Icon, Flex, Text, Select } from "@chakra-ui/core";
import { withSnackbar } from "notistack";
import { Container } from "@material-ui/core";

function LogRow({ Log_Field }) {
  return (
    <Box
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
      <Box d="flex" justifyContent="space-between" alignItems="center">
        <Box>
          <Box mt="1" lineHeight="tight" isTruncated>
            <Text fontWeight="semibold" as="h4">
              Source: {Log_Field["source"]}, Time: {Log_Field["timestamp"]}
            </Text>
          </Box>
          <Box>
            <Container>Log : {Log_Field["text"]}</Container>
          </Box>
        </Box>
      </Box>
    </Box>
  );
}

function TaskLog({ tid, TaskLogs }) {
  let logSources = ["all", "frontend", "automarker", "connector", "api"];
  let filter = "all"
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
            // justifyContent="center"
            justifyContent="space-between"
            alignItems="center"
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

            <Select variant="filled" placeholder="All" w="40%">
                {logSources.map((key, index) => (
                  <option value={key}>{key}</option>
                ))}
            </Select>

            
          </Flex>
          <Stack shouldWrapChildren spacing={4} ml={4} mt={4}>
            <Stack shouldWrapChildren spacing={2}>
              {TaskLogs.length !== 0 ? (
                TaskLogs.map((key, index) => (
                  <LogRow key={index} Log_Field={key} />
                ))
              ) : (
                <h1>No tasks to display</h1>
              )}
            </Stack>
          </Stack>
        </Box>
      </Box>
    </div>
  );
}

export default withSnackbar(TaskLog);
