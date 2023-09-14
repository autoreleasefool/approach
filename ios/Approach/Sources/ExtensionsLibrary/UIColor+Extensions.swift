import UIKit

extension UIColor {
	public var rgba: (red: CGFloat, green: CGFloat, blue: CGFloat, alpha: CGFloat) {
		var red: CGFloat = .zero
		var green: CGFloat = .zero
		var blue: CGFloat = .zero
		var alpha: CGFloat = .zero
		getRed(&red, green: &green, blue: &blue, alpha: &alpha)
		return (red, green, blue, alpha)
	}

	public static var random: UIColor {
		let red = (CGFloat.random(in: 0...1) + 1) / 2
		let green = (CGFloat.random(in: 0...1) + 1) / 2
		let blue = (CGFloat.random(in: 0...1) + 1) / 2

		return UIColor(red: red, green: green, blue: blue, alpha: 1)
	}
}
