import ComposableArchitecture
import Foundation
@testable import LaneEditorFeature
import XCTest

final class LaneEditorTests: XCTestCase {

	@MainActor
	func testTogglesAgainstWall() async {
		let id0 = UUID(0)
		let store = TestStore(initialState: LaneEditor.State(id: id0, label: "1", position: .noWall)) {
			LaneEditor()
		}

		XCTAssertEqual(store.state.position, .noWall)

		await store.send(\.binding.position, .leftWall) {
			$0.position = .leftWall
		}
	}
}
