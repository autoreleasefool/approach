import XCTest

public func assertThrowsError(_ block: () async throws -> Void) async {
	var caughtError: Error?
	do {
		try await block()
	} catch {
		caughtError = error
	}

	XCTAssertNotNil(caughtError)
}

public func assertNoThrowsError(_ block: () async throws -> Void) async {
	do {
		try await block()
	} catch {
		XCTFail("block threw error, \(error)")
	}
}
