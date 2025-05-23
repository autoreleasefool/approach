import SnapshotTesting
import SwiftUI
import Testing
import TestUtilitiesLibrary
@testable import ViewsLibrary

@Suite("ShareButton", .tags(.library), .snapshots(record: .failed))
struct ShareButtonTests {

	@Test("Share button snapshots", .tags(.snapshot))
	@MainActor
	func snapshotShareButton() {
		let shareButton = ShareButton { }
		assertSnapshot(of: shareButton, as: .image)
	}
}
