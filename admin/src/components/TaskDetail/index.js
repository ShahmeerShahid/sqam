import React from "react";
import { Box, Heading, Stack, Flex, Icon, ThemeProvider, Text} from "@chakra-ui/core";
import { withSnackbar } from "notistack";

function TaskRow({ Field_Name, Field_Value }) {
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
                  <Text>{Field_Name} : </Text>
                  </Flex>
                  <Box>
                  <Text>{Field_Value} </Text>
                  </Box>
              </Flex>
          </ThemeProvider>
      </div>
    );
}

function TaskDetail({TaskData, tid, enqueueSnackbar}) {
    return (
    <div >
        <Box
            backgroundColor="gainsboro"
            shadow="sm"
            borderRadius="lg"
            maxH ={1000}
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
                    {TaskData ? ( Object.keys(TaskData).map((key, index) => (
                        <TaskRow key={index} Field_Name={key} Field_Value={String(TaskData[key])} />
                    ))) : (
                        <h1>No tasks to display</h1>
                    )}
                </Stack>
            </Box>
    </div>
  );
}

export default withSnackbar(TaskDetail);
