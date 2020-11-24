import React from "react";
import { ThemeProvider, Box, Heading, Stack, Text, Flex, Icon} from "@chakra-ui/core";
import { withSnackbar } from "notistack";
import { useForm } from "react-hook-form";
import { useHistory } from "react-router-dom";
import { createTask } from "../../requests/tasks";


function TaskRow({
    Field_Name,
    Field_Value,
  
}) {
  return (
    <div>
        <ThemeProvider>
            <Flex
                bg="lightseagreen"
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

export default withSnackbar(TaskRow);
