import SnapshotTesting
import SwiftUI
import Testing
@testable import ViewsLibrary

struct EditButtonTests {
	@Test("Edit button snapshots", .tags(.snapshot))
	@MainActor
	func snapshotEditButton() {
		let editButton = EditButton { }
		assertSnapshot(of: editButton, as: .image)
	}
}
