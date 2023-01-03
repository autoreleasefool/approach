import SharedModelsMocksLibrary
import SnapshotTesting
import SwiftUI
import XCTest
@testable import SharedModelsViewsLibrary

final class TeamRowTests: XCTestCase {
	func testTeamRowSnapshot() {
		let rows = List {
			Section {
				TeamRow(team: .mock(id: UUID()))
				TeamRow(team: .mock(id: UUID(), name: "The Family"))
			}
		}

		let vc = UIHostingController(rootView: rows)
		vc.view.frame = UIScreen.main.bounds

		assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
	}
}
