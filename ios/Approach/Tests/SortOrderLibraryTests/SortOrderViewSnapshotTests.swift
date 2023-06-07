import ComposableArchitecture
import SnapshotTesting
@testable import SortOrderLibrary
import SwiftUI
import XCTest

@MainActor
final class SortOrderViewSnapshotTests: XCTestCase {
	func testSortOrderViewSnapshot() async {
		let view = SortOrderView(store: .init(
			initialState: SortOrder.State(initialValue: MockOrderable.first),
			reducer: SortOrder()
		))

		let vc = UIHostingController(rootView: view)
		vc.view.frame = UIScreen.main.bounds

		assertSnapshot(matching: vc, as: .image(on: .iPhone13ProMax))
	}
}
