import { Box, Stack } from "@chakra-ui/core";
import React from "react";
import ContributorsList from "../../components/ContributorsList";
import Header from "../../components/Header";
import Info from "../../components/Info";
import Instructions from "../../components/Instructions";

// landing page for the application 
export function HomePage() {
  return (
    <div>
      <Header />
      <Stack direction="row">
        <Box w="75%">
          {/* Information about the Automarker */}
          <Info />
          {/* Information about the Professors */}
          <Instructions />
        </Box>
        <Box w="25%">
          {/* Information about the Developers */}
          <ContributorsList />
        </Box>
      </Stack>
    </div>
  );
}
export default HomePage;
