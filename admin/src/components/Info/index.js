import { Stack, Text } from "@chakra-ui/core";
import React from "react";

//  Information about the Automarker 
const Info = () => {
  return (
    <Stack p={5} shadow="md" borderWidth="1px" m={12} textAlign="center">
      <Text opacity={0.8} fontSize={{ base: "lg", lg: "xl" }}>
        SQL Automarker (SQAM) is an automarking system allowing users to pull
        student submissions from multiple sources, configure the marking
        options, and view marking task results.
      </Text>
      <Text
        opacity={0.8}
        fontSize={{ base: "lg", lg: "xl" }}
        mt="6"
        textAlign="center"
      >
        The automarker supports partial marking, which leverages machine
        learning and string similarity comparison to provide students with
        partial marks.
      </Text>
      <Text
        opacity={0.8}
        fontSize={{ base: "lg", lg: "xl" }}
        mt="6"
        textAlign="center"
      >
        It was developed under the supervision of Dr. Michael Liut and Dr. Ilir
        Dema.
      </Text>
    </Stack>
  );
};

export default Info;
