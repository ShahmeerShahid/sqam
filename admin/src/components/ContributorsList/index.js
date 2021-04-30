import { Avatar, Heading, Stack, Text } from "@chakra-ui/core";
import alberto from "../../assets/img/alberto.png";
import erik from "../../assets/img/erik.png";
import jarrod from "../../assets/img/jarrod.jpg";
import sandy from "../../assets/img/sandy.PNG";
import shahmeer from "../../assets/img/shahmeer.png";
import vaishvik from "../../assets/img/vaishvik.PNG";

import React from "react";

// Component for individual contributor information
const Contributor = ({ name, team, duties, img }) => {
  return (
    <Stack paddingBottom={4} textAlign="center" alignItems="center">
      <Avatar src={img ? img : ""} />
      <Text as="strong">{name}</Text>
      <Text as="i">{team}</Text>
      <Text>{duties}</Text>
    </Stack>
  );
};

// All contributor information
const contributors = [
  {
    name: "Shahmeer Shahid",
    team: "Engineering Team",
    duties: "Architecture, Development, Leadership",
    img: shahmeer,
  },
  {
    name: "Sandy Wang",
    team: "Research/Engineering Team",
    duties: "ERD Automarker, Word Meaning Comparison, SQL Automarker (SQAM)",
    img: sandy,
  },
  {
    name: "Erik Holmes",
    team: "Engineering Team",
    duties: "SQL Automarker (SQAM)",
    img: erik,
  },
  {
    name: "Alberto Gateno",
    team: "Research Team",
    duties: "ERD Marker, Quantitative Graph Comparison",
    img: alberto,
  },
  {
    name: "Jarrod Servilla",
    team: "Engineering Team",
    duties: "Admin Site, Admin API",
    img: jarrod,
  },
  {
    name: "Vaishvik Maisuria",
    team: "Engineering/Research Team",
    duties: "Admin Site, Admin API, ERD Marker",
    img: vaishvik,
  },
];

// Component of all contributors
const ContributorsList = () => {
  return (
    <Stack p={5} shadow="md" borderWidth="1px" m={12} textAlign="center">
      <Heading as="h3" size="lg">
        Contributors
      </Heading>
      {contributors &&
        contributors.map((contributor, index) => {
          return <Contributor {...contributor} key={`contributor-${index}`} />;
        })}
    </Stack>
  );
};

export default ContributorsList;
