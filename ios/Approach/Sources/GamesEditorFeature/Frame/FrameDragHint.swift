import AssetsLibrary
import ComposableArchitecture
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import TipsLibrary

struct FrameDragHint: View {
	let onDismiss: () -> Void

	@State private var frameWidth: CGFloat = 0
	@State private var tipContentSize: CGSize = .zero

	var body: some View {
		GeometryReader { proxy in
			VStack(alignment: .leading, spacing: 0) {
				ViewThatFits {
					Rectangle()
						.fill(.clear)
						.frame(height: tipContentSize.height)

					EmptyView()
				}

				Spacer()

				Image(systemSymbol: .handDrawFill)
					.resizable()
					.scaledToFit()
					.foregroundColor(Asset.Colors.Primary.light)
					.frame(width: .largeIcon, height: .largeIcon)
					.keyframeAnimator(
						initialValue: AnimationValues(frameWidth: frameWidth)
					) { content, value in
						let animationWidth = value.frameWidth - CGFloat.standardSpacing * 2 - CGFloat.largeIcon

						content
							.opacity(value.opacity)
							.offset(x: value.xOffset * animationWidth + CGFloat.standardSpacing)
					} keyframes: { _ in
						KeyframeTrack(\.opacity) {
							LinearKeyframe(1.0, duration: 0.5, timingCurve: .easeInOut)
							LinearKeyframe(1.0, duration: 1.0, timingCurve: .linear)
							LinearKeyframe(0.0, duration: 0.5, timingCurve: .easeInOut)
						}

						KeyframeTrack(\.xOffset) {
							LinearKeyframe(0.0, duration: 0.5, timingCurve: .linear)
							LinearKeyframe(1.0, duration: 1.0, timingCurve: .easeInOut)
							LinearKeyframe(1.0, duration: 0.5, timingCurve: .linear)
						}
					}
					.onChange(of: proxy.size.width, initial: true) { _, newValue in
						guard newValue > 0 else { return }
						frameWidth = newValue
					}

				Spacer()

				ShortTipView(tip: .frameDragTip, onDismiss: onDismiss)
					.padding()
					.background(
						RoundedRectangle(cornerRadius: .standardRadius)
							.fill(Asset.Colors.Background.default.swiftUIColor)
					)
					.padding()
					.measure(key: TipContentSizeKey.self, to: $tipContentSize)
			}
			.frame(maxWidth: .infinity, alignment: .leading)
			.background(
				Rectangle()
					.fill(
							.linearGradient(
							.init(stops: [
								.init(color: .black.opacity(0.0), location: 0.0),
								.init(color: .black.opacity(0.6), location: 0.05),
								.init(color: .black.opacity(0.6), location: 0.95),
								.init(color: .black.opacity(0.0), location: 1.0),
							]),
							startPoint: .top,
							endPoint: .bottom
						)
					)
			)
		}
	}
}

private struct AnimationValues {
	let frameWidth: CGFloat
	var opacity: CGFloat = 0
	var xOffset: CGFloat = 0
}

extension Tip {
	static let frameDragTip = Tip(title: Strings.Frame.Editor.DragHint.message)
}

private struct TipContentSizeKey: PreferenceKey, CGSizePreferenceKey {}
