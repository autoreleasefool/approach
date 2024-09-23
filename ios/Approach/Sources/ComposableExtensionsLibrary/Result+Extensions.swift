extension Result where Failure == Swift.Error {
	public static func `of`(_ body: @Sendable () async throws -> Success) async -> Self {
		do {
			return .success(try await body())
		} catch {
			return .failure(error)
		}
	}
}
