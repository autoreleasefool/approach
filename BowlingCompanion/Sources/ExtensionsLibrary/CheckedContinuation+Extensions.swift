extension CheckedContinuation<Void, Error> {
	@Sendable public func resumeOrThrow(_ error: Error?) {
		if let error {
			self.resume(throwing: error)
		} else {
			self.resume(returning: ())
		}
	}
}
