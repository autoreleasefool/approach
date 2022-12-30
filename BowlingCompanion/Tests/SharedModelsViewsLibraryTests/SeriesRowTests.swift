import SharedModelsMocksLibrary
import SnapshotTesting
import SwiftUI
import XCTest
@testable import SharedModelsViewsLibrary

final class SeriesRowTests: XCTestCase {
	func testSeriesRowSnapshot() {
		let rows = List {
			Section {
				SeriesRow(series: .mock(league: UUID(), id: UUID(), date: .now))
				SeriesRow(series: .mock(league: UUID(), id: UUID(), date: .distantFuture))
				SeriesRow(series: .mock(league: UUID(), id: UUID(), date: .distantPast))
			}
		}

		let vc = UIHostingController(rootView: rows)
		vc.view.frame = UIScreen.main.bounds

		assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
	}
}
