import XCTest

public func assertThrowsError<T>(ofType: T.Type = Any.self, _ block: () async throws -> Void) async {
	var caughtError: Error?
	do {
		try await block()
	} catch {
		caughtError = error
	}

	XCTAssertNotNil(caughtError)
	XCTAssertTrue(caughtError is T)
}

public func assertNoThrowsError(_ block: () async throws -> Void) async {
	do {
		try await block()
	} catch {
		XCTFail("block threw error, \(error)")
	}
}
