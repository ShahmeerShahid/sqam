import React, {useState} from "react";
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
	Stack
} from "@chakra-ui/core";


import {login} from './loginfunc';
import ErrorMsg from '../../components/ErrorMsg';
import { Link } from "react-router-dom";


export function LoginForm(){
		const [username, setUsername] = useState(null);
		const [password, setPassword] = useState(null);
		const [error, setError] = useState(null);
		const [loading, setLoading] = useState(false);
		const [loggedIn, setLoggedIn] = useState(false);
		const handleSubmit = async event => {
			event.preventDefault();
			setLoading(true);
			try {
			  await login({ username, password });
			  setLoggedIn(true);
			  setLoading(false);
			} catch (error) {
			  setError('Invalid username or password');
			  setLoading(false);
			  setUsername(null);
			  setPassword(null);
			}
		  };
	return(
		
		<div className = "myDiv">
			<Flex w = "full" align = "center" justifyContent = "center" height = "100vh">
				<Stack>
					<Box p = {20} maxWidth = "1000px" borderWidth = {2} borderRadius = {8} boxShadow = "xl">
					{loggedIn? (
						<Box textAlign="center">
						<Text>liutmich logged in!
						<Button variantColor="orange"
						variant="outline"
						width="full"
						mt={4}
						>
						<Link to = "/tasks">
						click here to continue
						</Link>
						</Button>
						</Text>
						</Box>
					
						) : (
						<>
						<Box textAlign="center">
							<Heading size = "2xl">Login</Heading>
						</Box>
						<Box textAlign = "left">
							<form onSubmit = {handleSubmit}>
							{error && <ErrorMsg message={error} />}
								<FormControl mt = {4}>
									<FormLabel>Username</FormLabel>
									<Input type = "username" placeholder = "example@youremail.com"
									onChange = {event => setUsername(event.currentTarget.value)}/>
								</FormControl>
								<FormControl mt = {4}>
									<FormLabel>Password</FormLabel>
									<Input type = "password"
									onChange = {event => setPassword(event.currentTarget.value)}
									/>
								</FormControl>
								<Button w = "full" variantColor = "blue" variant = "solid" mt = {4} type = "submit">
									{loading ? (
										<CircularProgress isIndeterminate color = "green.400" size="24px"/>
									) : (
										'Sign In'
									)}
								</Button>
								<Link to = "/">
									Forget password?
								</Link>
							</form>  
						</Box>
						</>
					)}
					</Box>
					<Link to = "/">
					<Button variantColor = "blue" variant = "solid" w="full">	
						BACK
					</Button>
					</Link>
				</Stack>	
			</Flex>
		</div>

    );
}

export default LoginForm;
