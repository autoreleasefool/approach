import Foundation
import SnapshotTesting
import Testing
import TestUtilitiesLibrary
@testable import ToastLibrary

@Suite("HUDView Tests", .tags(.library), .snapshots(record: .failed))
struct HUDViewTests {

	@Test("HUD view", .tags(.snapshot))
	@MainActor
	func snapshotHUDView() {
		let hudView = HUDView(
			title: "HUD Title",
			message: nil,
			action: nil,
			systemImage: nil,
			style: .primary,
			onAction: { },
			onDismiss: { }
		)
		.frame(width: 428)

		assertSnapshot(of: hudView, as: .image)
	}

	@Test("HUD view with message", .tags(.snapshot))
	@MainActor
	func snapshotWithMessage() {
		let hudView = HUDView(
			title: "HUD Title",
			message: "HUD Message",
			action: nil,
			systemImage: nil,
			style: .primary,
			onAction: { },
			onDismiss: { }
		)
		.frame(width: 428)

		assertSnapshot(of: hudView, as: .image)
	}

	@Test("HUD view with action", .tags(.snapshot))
	@MainActor
	func snapshotWithAction() {
		let hudView = HUDView(
			title: "HUD Title",
			message: nil,
			action: "Action",
			systemImage: nil,
			style: .primary,
			onAction: { },
			onDismiss: { }
		)
		.frame(width: 428)

		assertSnapshot(of: hudView, as: .image)
	}

	@Test("HUD view with systemImage", .tags(.snapshot))
	@MainActor
	func snapshotWithSystemImage() {
		let hudView = HUDView(
			title: "HUD Title",
			message: nil,
			action: nil,
			systemImage: "figure.bowling",
			style: .primary,
			onAction: { },
			onDismiss: { }
		)
		.frame(width: 428)

		assertSnapshot(of: hudView, as: .image)
	}

	@Test("HUD view with error style", .tags(.snapshot))
	@MainActor
	func snapshotWithError() {
		let hudView = HUDView(
			title: "HUD Title",
			message: nil,
			action: nil,
			systemImage: nil,
			style: .error,
			onAction: { },
			onDismiss: { }
		)
		.frame(width: 428)

		assertSnapshot(of: hudView, as: .image)
	}

	@Test("HUD view with all properties", .tags(.snapshot))
	@MainActor
	func snapshotWithAllProperties() {
		let hudView = HUDView(
			title: "HUD Title",
			message: "HUD Message",
			action: "Action",
			systemImage: "figure.bowling",
			style: .success,
			onAction: { },
			onDismiss: { }
		)
		.frame(width: 428)

		assertSnapshot(of: hudView, as: .image)
	}
}
