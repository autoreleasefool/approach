import SnapshotTesting
import SwiftUI
import Testing
import TestUtilitiesLibrary
@testable import ViewsLibrary

@Suite("CenteredScrollView", .tags(.library), .snapshots(record: .failed))
struct CenteredScrollViewTests {

	@Test("CenteredScrollView snapshots", .tags(.snapshot))
	@MainActor
	func snapshotCenteredScrollView() {
		let scrollView = CenteredScrollView {
			Text("Content")
		}
			.frame(width: 428, height: 500)

		assertSnapshot(of: scrollView, as: .image)
	}
}
