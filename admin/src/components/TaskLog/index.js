import React from "react";
import { Box, Heading, Stack, Text, Icon, Flex, Tag } from "@chakra-ui/core";
import { withSnackbar } from "notistack";
import { useForm } from "react-hook-form";
import { useHistory } from "react-router-dom";
import { createTask } from "../../requests/tasks";


function TaskLog({
    tid
}) {
    return (
        <div>
            <Box>
                <Box
                    backgroundColor="white"
                    borderRadius="lg"
                    shadow="sm"
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
                    </Flex>
                    <Stack shouldWrapChildren spacing={4} ml={4} mt={4}>
                        <Stack shouldWrapChildren spacing={2}>
                            
                        </Stack>
                    </Stack>
                </Box>
            </Box>
        </div>
    );
}

export default withSnackbar(TaskLog);
