import SharedModelsMocksLibrary
import SnapshotTesting
import SwiftUI
import XCTest
@testable import SharedModelsViewsLibrary

final class LeagueRowTests: XCTestCase {
	func testLeagueRowSnapshot() {
		let rows = List {
			Section {
				LeagueRow(league: .mock(bowler: UUID(), id: UUID()))
				LeagueRow(league: .mock(bowler: UUID(), id: UUID(), name: "Majors, 2021"))
			}
		}

		let vc = UIHostingController(rootView: rows)
		vc.view.frame = UIScreen.main.bounds

		assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
	}
}
