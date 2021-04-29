export class HTTPError extends Error {
  errorCode: 400 | 404 | 500;
  constructor(message: string, errorCode: 400 | 404 | 500) {
    super(message);
    this.errorCode = errorCode;
  }
}

export class NotFoundError extends HTTPError {
  constructor(message: string) {
    super(message, 404);
  }
}

export class BadRequestError extends HTTPError {
  constructor(message: string) {
    super(message, 400);
  }
}
