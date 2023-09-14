import AvatarServiceInterface
import Dependencies
import ExtensionsLibrary
import Foundation
import ModelsLibrary
import UIKit

extension AvatarService: DependencyKey {
	public static var liveValue: Self = {
		let cache = Cache()

		@Sendable func render(text: String, color: Avatar.Background) -> UIImage {
			let imageSize: CGFloat = 256
			let renderer = UIGraphicsImageRenderer(size: CGSize(width: imageSize, height: imageSize))
			return renderer.image { ctx in
				let backgroundColor = color.uiColor
				let rect = CGRect(x: 0, y: 0, width: imageSize, height: imageSize)

				backgroundColor.setFill()
				ctx.cgContext.fillEllipse(in: rect)

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

		@Sendable func render(_ avatar: Avatar.Summary) async -> UIImage? {
			if let image = await cache.fetch(avatar.value) {
				return image
			}

			let image: UIImage?
			switch avatar.value {
			case let .url(url):
				if url.isFileURL {
					image = UIImage(contentsOfFile: url.absoluteString)
				} else {
					image = UIImage()
				}
			case let .data(data):
				image = UIImage(data: data)
			case let .text(text, color):
				image = render(text: text, color: color)
			}

			guard let image else { return nil }
			await cache.store(avatar.value, image: image)
			return image
		}

		return .init(
			render: render(_:),
			preRender: {
				_ = await render($0)
			}
		)
	}()
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
