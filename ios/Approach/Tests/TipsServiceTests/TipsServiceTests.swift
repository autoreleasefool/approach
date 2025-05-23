import Dependencies
import Foundation
import Testing
import TestUtilitiesLibrary
import TipsLibrary
import UserDefaultsPackageServiceInterface
@testable import TipsService
@testable import TipsServiceInterface

@Suite("TipsService", .tags(.service))
struct TipsServiceTests {
	@Dependency(TipsService.self) var tips

	@Test("Should show an unshown tip", .tags(.unit))
	func shouldShowAnUnshownTip() {
		let tip = Tip(id: "1", title: "Tip 1", message: "Message 1")

		let shouldShow = withDependencies {
			$0.userDefaults.bool = { _ in false }
			$0[TipsService.self] = .liveValue
		} operation: {
			tips.shouldShow(tipFor: tip)
		}

		#expect(shouldShow == true)
	}

	@Test("Should not show a shown tip", .tags(.unit))
	func shouldNotShowAShownTip() {
		let tip = Tip(id: "1", title: "Tip 1", message: "Message 1")

		let shouldShow = withDependencies {
			$0.userDefaults.bool = { _ in true }
			$0[TipsService.self] = .liveValue
		} operation: {
			tips.shouldShow(tipFor: tip)
		}

		#expect(shouldShow == false)
	}

	@Test("Should hide a tip", .tags(.unit))
	func shouldHideATip() async {
		let tip = Tip(id: "1", title: "Tip 1", message: "Message 1")

		await confirmation { tipHidden in
			await withDependencies {
				$0.userDefaults.setBool = { key, value in
					#expect(key == "tipsService.\(tip.id)")
					#expect(value == true)
					tipHidden()
				}
				$0[TipsService.self] = .liveValue
			} operation: {
				await tips.hide(tipFor: tip)
			}
		}
	}
}
