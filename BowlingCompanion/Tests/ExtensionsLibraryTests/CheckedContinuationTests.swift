import ExtensionsLibrary
import XCTest

final class CheckedContinuationTests: XCTestCase {
	func testResumeOrThrow_Resumes() async throws {
		func resumes(onComplete: (Error?) -> Void) {
			onComplete(nil)
		}

		do {
			try await withCheckedThrowingContinuation { (c: CheckedContinuation<Void, Error>) in
				resumes { error in
					c.resumeOrThrow(error)
				}
			}
		} catch {
			XCTFail("Did not expect error to be thrown: \(error)")
		}
	}

	func testResumeOrThrow_Throws() async throws {
		func throwsError(onComplete: (Error?) -> Void) {
			onComplete(MockError.mock)
		}

		let expectation = self.expectation(description: "error thrown")
		do {
			try await withCheckedThrowingContinuation { (c: CheckedContinuation<Void, Error>) in
				throwsError { error in
					c.resumeOrThrow(error)
				}
			}
		} catch {
			expectation.fulfill()
		}

		wait(for: [expectation], timeout: 1)
	}
}

private enum MockError: Error {
	case mock
}
