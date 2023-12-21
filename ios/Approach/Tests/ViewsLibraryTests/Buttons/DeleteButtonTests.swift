import SnapshotTesting
import SwiftUI
@testable import ViewsLibrary
import XCTest

final class DeleteButtonTests: XCTestCase {
	func testDeleteButtonSnapshot() {
		let deleteButton = DeleteButton { }

		let vc = UIHostingController(rootView: deleteButton)
		vc.view.frame = UIScreen.main.bounds

		assertSnapshot(matching: vc, as: .image(on: .iPhoneSe))
	}
}
