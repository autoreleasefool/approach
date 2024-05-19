import AvatarServiceInterface
import Dependencies
import ExtensionsPackageLibrary
import Foundation
import ModelsLibrary
import UIKit

extension AvatarService: DependencyKey {
	public static var liveValue: Self {
		let cache = Cache()

		@Sendable func render(text: String, background: Avatar.Background) -> UIImage {
			let imageSize: CGFloat = 256
			let renderer = UIGraphicsImageRenderer(size: CGSize(width: imageSize, height: imageSize))
			return renderer.image { ctx in
				let rect = CGRect(x: 0, y: 0, width: imageSize, height: imageSize)
				let backgroundColor: UIColor

				switch background {
				case let .rgb(solid):
					backgroundColor = solid.uiColor
					backgroundColor.setFill()
					ctx.cgContext.fillEllipse(in: rect)
				case let .gradient(first, second):
					backgroundColor = first.uiColor.averaged(with: second.uiColor)

					let gradientColors = [first.uiColor.cgColor, second.uiColor.cgColor] as CFArray
					let colorSpace = CGColorSpaceCreateDeviceRGB()
					let locations: [CGFloat] = [0, 1]
					if let gradient = CGGradient(colorsSpace: colorSpace, colors: gradientColors, locations: locations) {
						ctx.cgContext.drawLinearGradient(
							gradient,
							start: .zero,
							end: .init(x: rect.width, y: rect.height),
							options: []
						)
					}
				}

				let font = UIFont.boldSystemFont(ofSize: imageSize / 2)
				let paragraphStyle = NSMutableParagraphStyle()
				paragraphStyle.alignment = .center
				let attributes = [
					NSAttributedString.Key.font: font,
					NSAttributedString.Key.foregroundColor: backgroundColor.preferredForegroundColorForBackground,
					NSAttributedString.Key.paragraphStyle: paragraphStyle,
				]

				let initials = text.initials
				initials.draw(in: rect.insetBy(dx: 0, dy: (rect.height - font.lineHeight) / 2).integral, withAttributes: attributes)
			}
		}

		@Sendable func render(_ avatar: Avatar.Value) async -> UIImage? {
			if let image = await cache.fetch(avatar) {
				return image
			}

			let image: UIImage?
			switch avatar {
			case let .url(url):
				if url.isFileURL {
					image = UIImage(contentsOfFile: url.absoluteString)
				} else {
					image = UIImage()
				}
			case let .data(data):
				image = UIImage(data: data)
			case let .text(text, background):
				image = render(text: text, background: background)
			}

			guard let image else { return nil }
			await cache.store(avatar, image: image)
			return image
		}

		return Self(
			render: render(_:),
			preRender: {
				_ = await render($0)
			}
		)
	}
}

private actor Cache {
	var imageCache: NSCache<NSString, UIImage> = {
		var cache = NSCache<NSString, UIImage>()
		cache.countLimit = 300
		return cache
	}()

	func fetch(_ avatar: Avatar.Value) async -> UIImage? {
		return imageCache.object(forKey: NSString(string: String(describing: avatar)))
	}

	func store(_ avatar: Avatar.Value, image: UIImage) async {
		imageCache.setObject(image, forKey: NSString(string: String(describing: avatar)))
	}
}

extension String {
	var words: [String] {
		components(separatedBy: .alphanumerics.inverted)
			.filter { !$0.isEmpty }
	}

	var initials: String {
		let words = self.words
		if words.count > 1 {
			return words.prefix(2)
				.map { $0.first?.description ?? "" }
				.joined()
		} else {
			return String(prefix(2))
		}
	}
}
