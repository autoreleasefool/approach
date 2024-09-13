import SnapshotTesting
import SwiftUI
import Testing
@testable import ViewsLibrary

struct FilterButtonTests {
	@Test("Active filter button snapshot", .tags(.snapshot))
	@MainActor func snapshotActiveFilterButton() {
		let filterButton = FilterButton(isActive: true) { }
		assertSnapshot(of: filterButton, as: .image)
	}

	@Test("Inactive filter button snapshot", .tags(.snapshot))
	@MainActor func snapshotInactiveFilterButton() {
		let filterButton = FilterButton(isActive: false) { }
		assertSnapshot(of: filterButton, as: .image)
	}
}
