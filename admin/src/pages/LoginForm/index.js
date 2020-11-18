import React, {useState} from "react";
import {
    Box,
    Button,
    FormControl,
    FormLabel,
    Input,
    Flex,
	Heading,
	Link
} from "@chakra-ui/core";


import {demo} from './demo';
import ErrorMsg from '../../components/ErrorMsg';
import SuccessMsg from '../../components/SuccessMsg';

export function LoginForm(){
		const [username, setUsername] = useState('');
		const [password, setPassword] = useState('');
		const [error, setError] = useState('');
		const [success, setSuccess] = useState('');
		const handleSubmit = async event => {
			event.preventDefault();
			try {
			  await demo({ username, password });
			  setSuccess('You have successfully logged in');
			} catch (error) {
			  setError('Invalid username or password');
			  setUsername('');
			  setPassword('');
			}
		  };
	return(
		
		<div class = "myDiv">
			<Flex w = "full" algin = "center" justifyCotent = "center">
				<Box p = {20} maxWidth = "1000px" borderWidth = {2} borderRadius = {8} boxShadow = "xl">
				<Box textAlign = "center">
				<Heading size = "2xl">LoginForm</Heading>
				</Box>
				<Box textAlign = "left">
					<form onSubmit = {handleSubmit}>
					{error && <ErrorMsg message={error} />}
					{success && <SuccessMsg message = {success}/>}
						<FormControl>
							<FormLabel>Username</FormLabel>
							<Input type = {username} placeholder = "example@youremail.com"
							onChange = {event => setUsername(event.currentTarget.value)}/>
						</FormControl>
						<FormControl mt = {4}>
							<FormLabel>Password</FormLabel>
							<Input type = {password}
							onChange = {event => setPassword(event.currentTarget.value)}
							/>
						</FormControl>
						<Button w = "full" variantColor = "blue" variant = "solid" mt = {4} type = "submit">
							Sign in
						</Button>
						<Link href = "htttp://q.utoronto.ca">
							Forget password?
						</Link>
					</form>  
				</Box>
				</Box>	
			</Flex>
		</div>

    );
}

export default LoginForm;
