import SwiftUI

public enum UndimmedPresentationDetent {
	case large
	case medium

	case fraction(_ value: CGFloat)
	case height(_ value: CGFloat)

	var swiftUIDetent: PresentationDetent {
		switch self {
		case .large:
			return .large
		case .medium:
			return .medium
		case let .fraction(value):
			return .fraction(value)
		case let .height(value):
			return .height(value)
		}
	}

	var uiKitIdentifier: UISheetPresentationController.Detent.Identifier {
		switch self {
		case .large:
			return .large
		case .medium:
			return .medium
		case let .fraction(value):
			return .fraction(value)
		case let .height(value):
			return .height(value)
		}
	}
}

extension UISheetPresentationController.Detent.Identifier {
	static func fraction(_ value: CGFloat) -> Self {
		.init("Fraction:\(String(format: "%.1f", value))")
	}

	static func height(_ value: CGFloat) -> Self {
		.init("Height:\(value)")
	}
}

extension Collection where Element == UndimmedPresentationDetent {
	var swiftUISet: Set<PresentationDetent> {
		Set(map { $0.swiftUIDetent })
	}
}

private class UndimmedDetentController: UIViewController {
	var largestUndimmed: UndimmedPresentationDetent?

	override func viewWillAppear(_ animated: Bool) {
		super.viewWillAppear(animated)
		avoidDimmingParent()
		avoidDisablingControls()
	}

	func avoidDimmingParent() {
		let id = largestUndimmed?.uiKitIdentifier
		sheetPresentationController?.largestUndimmedDetentIdentifier = id
	}

	func avoidDisablingControls() {
		presentingViewController?.view.tintAdjustmentMode = .normal
	}
}

private struct UndimmedDetentView: UIViewControllerRepresentable {
	var largestUndimmed: UndimmedPresentationDetent?

	func makeUIViewController(context: Context) -> some UIViewController {
		let result = UndimmedDetentController()
		result.largestUndimmed = largestUndimmed
		return result
	}

	func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {}
}

extension View {
	public func presentationDetents(
		undimmed detents: [UndimmedPresentationDetent],
		largestUndimmed: UndimmedPresentationDetent? = nil
	) -> some View {
		self.background(UndimmedDetentView(largestUndimmed: largestUndimmed ?? detents.last))
			.presentationDetents(detents.swiftUISet)
	}

	public func presentationDetents(
		undimmed detents: [UndimmedPresentationDetent],
		largestUndimmed: UndimmedPresentationDetent? = nil,
		selection: Binding<PresentationDetent>
	) -> some View {
		self.background(UndimmedDetentView(largestUndimmed: largestUndimmed ?? detents.last))
			.presentationDetents(detents.swiftUISet, selection: selection)
	}
}
