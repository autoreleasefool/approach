import SwiftUI

enum AnimationUtils {
	static func wiggleAnimation(interval: TimeInterval, variance: Double) -> Animation {
		Animation
			.easeInOut(duration: randomize(interval: interval, withVariance: variance))
			.repeatForever(autoreverses: true)
	}

	static func randomize(interval: TimeInterval, withVariance variance: Double) -> TimeInterval {
		interval + variance * Double.random(in: -1...1)
	}
}

struct WiggleRotationModifier: ViewModifier {
	@Binding var isWiggling: Bool
	var rotationAmount: Double

	public func body(content: Content) -> some View {
		content
			.rotationEffect(Angle(degrees: isWiggling ? rotationAmount : 0))
			.animation(
				isWiggling ? AnimationUtils.wiggleAnimation(interval: 0.14, variance: 0.025) : .default,
				value: isWiggling
			)
	}
}

struct WiggleBounceModifier: GeometryEffect {
	var amount: Double
	var bounceAmount: Double

	var animatableData: Double {
		get { amount }
		set { amount = newValue }
	}

	func effectValue(size _: CGSize) -> ProjectionTransform {
		let bounce = sin(.pi * 2 * animatableData) * bounceAmount
		let translationEffect = CGAffineTransform(translationX: 0, y: CGFloat(bounce))
		return ProjectionTransform(translationEffect)
	}
}

extension View {
	public func wiggling(isWiggling: Binding<Bool>, rotationAmount: Double = 3, bounceAmount: Double = 1) -> some View {
		self
			.modifier(WiggleRotationModifier(isWiggling: isWiggling, rotationAmount: rotationAmount))
			.modifier(WiggleBounceModifier(amount: isWiggling.wrappedValue ? 1 : 0, bounceAmount: bounceAmount))
			.animation(
				isWiggling.wrappedValue
					? AnimationUtils.wiggleAnimation(interval: 0.3, variance: 0.025).repeatForever(autoreverses: true)
					: .default,
				value: isWiggling.wrappedValue
			)
	}
}
