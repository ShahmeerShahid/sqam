import React, { Component } from "react";

export default class Terminal extends Component {
  constructor(props) {
    super(props);
    this.state = {
      text: ""
    };
    this.print = this.print.bind(this);
    this.println = this.println.bind(this);
  }
  println(str = "", source = "", time = "", delay = null) {
    return this.print(`> ${source} -- [${time}] ${str}\n`, delay);
  }
  print(str = "", delay = null) {
    if (delay === null) {
      this.setState(({ text }) => ({
        text: `${text}${str}`
      }));
      return null;
    }
    return new Promise((resolve) => {
      setTimeout(
        () =>
          this.setState(
            ({ text }) => ({
              text: `${text}${str}`
            }),
            resolve
          ),
        delay
      );
    });
  }
  componentDidMount() {
    this.props.children({ print: this.print, println: this.println });
  }
  render() {
    const { text } = this.state;
    return <pre>{text}</pre>;
  }
}
