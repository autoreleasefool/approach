import SharedModelsMocksLibrary
@testable import SharedModelsViewsLibrary
import SnapshotTesting
import SwiftUI
import XCTest

final class SeriesRowTests: XCTestCase {
	func testSeriesRowSnapshot() {
		let rows = List {
			Section {
				SeriesRow(series: .mock(league: UUID(), id: UUID(), date: Date(timeIntervalSince1970: 1672519204)))
				SeriesRow(series: .mock(league: UUID(), id: UUID(), date: .distantFuture))
				SeriesRow(series: .mock(league: UUID(), id: UUID(), date: .distantPast))
			}
		}

		print(Date.now.timeIntervalSince1970)

		let vc = UIHostingController(rootView: rows)
		vc.view.frame = UIScreen.main.bounds

		assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
	}
}
