class ApiError extends Error {
  constructor(statusCode, code, message) {
    super(message);
    this.statusCode = statusCode;
    this.code = code;
  }

  static unauthenticated(message = 'Missing or invalid authentication token.') {
    return new ApiError(401, 'UNAUTHENTICATED', message);
  }

  static forbidden(message = 'You do not have access to this resource.') {
    return new ApiError(403, 'FORBIDDEN', message);
  }

  static notFound(code, message) {
    return new ApiError(404, code, message);
  }

  static validation(message) {
    return new ApiError(422, 'VALIDATION_ERROR', message);
  }
}

module.exports = ApiError;
