// Page not Used!
import React, { useState } from "react";
import {
  Box,
  Button,
  FormControl,
  FormLabel,
  Input,
  Flex,
  Heading,
  Text,
  CircularProgress,
  Stack,
} from "@chakra-ui/core";

import { login } from "../../requests/login/";
import ErrorMsg from "../../components/ErrorMsg";
import { Link } from "react-router-dom";

export function LoginForm() {
  const [username, setUsername] = useState(null);
  const [password, setPassword] = useState(null);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [loggedIn, setLoggedIn] = useState(false);
  const handleSubmit = async (event) => {
    event.preventDefault();
    setLoading(true);
    try {
      const response = await login({ username, password });
      setLoggedIn(response);
      setLoading(false);
      if (!response) {
        setError("Invalid username or password");
        setLoading(false);
      }
    } catch (error) {
      setError("Invalid username or password");
      setLoading(false);
    }
  };
  return (
    <div className="myDiv">
      <Flex w="full" align="center" justifyContent="center" height="100vh">
        <Stack>
          {error && <ErrorMsg message={error} />}
          <Box
            p={20}
            maxWidth="1000px"
            borderWidth={2}
            borderRadius={8}
            boxShadow="xl"
          >
            {loggedIn ? (
              <Box textAlign="center">
                <Text>
                  liutmich logged in!
                  <Button
                    variantColor="orange"
                    variant="outline"
                    width="full"
                    mt={4}
                  >
                    <Link to="/tasks">click here to continue</Link>
                  </Button>
                </Text>
              </Box>
            ) : (
              <>
                <Box textAlign="center">
                  <Heading size="2xl">Login</Heading>
                </Box>
                <Box textAlign="left">
                  <form onSubmit={handleSubmit}>
                    {/* {error && <ErrorMsg message={error} />} */}
                    <FormControl mt={4} pattern="(@)(.+)" isRequired>
                      <FormLabel>Username</FormLabel>
                      <Input
                        type="email"
                        placeholder="example@youremail.com"
                        onChange={(event) =>
                          setUsername(event.currentTarget.value)
                        }
                      />
                    </FormControl>
                    <FormControl mt={4} isRequired>
                      <FormLabel>Password</FormLabel>
                      <Input
                        type="password"
                        onChange={(event) =>
                          setPassword(event.currentTarget.value)
                        }
                      />
                    </FormControl>
                    <Button
                      w="full"
                      variantColor="blue"
                      variant="solid"
                      mt={4}
                      type="submit"
                    >
                      {loading ? (
                        <CircularProgress
                          isIndeterminate
                          color="green.400"
                          size="24px"
                        />
                      ) : (
                        "Sign In"
                      )}
                    </Button>
                    <Link to="/">
                      <Text textAlign="center">Forgot password?</Text>
                    </Link>
                  </form>
                </Box>
              </>
            )}
          </Box>
          <Link to="/">
            <Button variantColor="blue" variant="solid" w="full">
              BACK
            </Button>
          </Link>
        </Stack>
      </Flex>
    </div>
  );
}

export default LoginForm;
