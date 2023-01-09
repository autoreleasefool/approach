import SharedModelsMocksLibrary
import SnapshotTesting
import SwiftUI
import XCTest
@testable import SharedModelsViewsLibrary

final class OpponentRowTests: XCTestCase {
	func testOpponentRowSnapshot() {
		let rows = List {
			Section {
				OpponentRow(opponent: .mock(id: UUID()))
				OpponentRow(opponent: .mock(id: UUID(), name: "Sarah"))
			}
		}

		let vc = UIHostingController(rootView: rows)
		vc.view.frame = UIScreen.main.bounds

		assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
	}
}
