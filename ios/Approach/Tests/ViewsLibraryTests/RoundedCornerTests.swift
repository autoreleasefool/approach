import SnapshotTesting
import SwiftUI
import Testing
import TestUtilitiesLibrary
@testable import ViewsLibrary

@Suite("RoundCorners", .tags(.library), .snapshots(record: .failed))
struct RoundCornersTests {

	@Test("Top leading corner snapshot", .tags(.snapshot))
	@MainActor
	func snapshotTopLeadingCorner() {
		let content = VStack {
			Color.black
				.frame(width: 100, height: 100)
				.roundCorners(topLeading: true)
		}
		assertSnapshot(of: content, as: .image)
	}

	@Test("Top trailing corner snapshot", .tags(.snapshot))
	@MainActor
	func snapshotTopTrailingCorner() {
		let content = VStack {
			Color.black
				.frame(width: 100, height: 100)
				.roundCorners(topTrailing: true)
		}
		assertSnapshot(of: content, as: .image)
	}

	@Test("Bottom leading corner snapshot", .tags(.snapshot))
	@MainActor
	func snapshotBottomLeadingCorner() {
		let content = VStack {
			Color.black
				.frame(width: 100, height: 100)
				.roundCorners(bottomLeading: true)
		}
		assertSnapshot(of: content, as: .image)
	}

	@Test("Bottom trailing corner snapshot", .tags(.snapshot))
	@MainActor
	func snapshotBottomTrailingCorner() {
		let content = VStack {
			Color.black
				.frame(width: 100, height: 100)
				.roundCorners(bottomTrailing: true)
		}
		assertSnapshot(of: content, as: .image)
	}

	@Test("Multiple corners snapshot", .tags(.snapshot))
	@MainActor
	func snapshotMultipleCorners() {
		let content = VStack {
			Color.black
				.frame(width: 100, height: 100)
				.roundCorners(topLeading: true, topTrailing: true, bottomLeading: true, bottomTrailing: true)
		}
		assertSnapshot(of: content, as: .image)
	}
}
