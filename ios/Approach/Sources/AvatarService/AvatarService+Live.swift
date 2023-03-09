import AvatarServiceInterface
import Dependencies
import Foundation
import SharedModelsLibrary
import UIKit

extension AvatarService: DependencyKey {
	public static let liveValue: Self = {
		let cache = Cache()

		@Sendable func render(text: String, color: Avatar.Background) -> UIImage {
			let renderer = UIGraphicsImageRenderer(size: CGSize(width: 256, height: 256))
			return renderer.image { ctx in
				color.uiColor.setFill()
				ctx.cgContext.fillEllipse(in: CGRect(x: 0, y: 0, width: 256, height: 256))

				let paragraphStyle = NSMutableParagraphStyle()
				paragraphStyle.alignment = .center

				let attrs = [NSAttributedString.Key.font: UIFont(name: "HelveticaNeue-Thin", size: 36)!, NSAttributedString.Key.paragraphStyle: paragraphStyle]

				let string = String(text.prefix(2))
				string.draw(with: CGRect(x: 32, y: 32, width: 448, height: 448), options: .usesLineFragmentOrigin, attributes: attrs, context: nil)
			}
		}

		@Sendable func render(_ avatar: Avatar) async -> UIImage? {
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
			case let .text(text, color):
				image = render(text: text, color: color)
			}

			guard let image else { return nil }
			await cache.store(avatar, image: image)
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
	var imageCache: NSCache<NSString, UIImage> = NSCache()

	private func cacheKey(for avatar: Avatar) -> NSString? {
		switch avatar {
		case let .url(url):
			return NSString(string: url.absoluteString)
		case  .data:
			return nil
		case let .text(text, color):
			return NSString(string: "\(text).\(String(describing: color))")
		}
	}

	func fetch(_ avatar: Avatar) async -> UIImage? {
		guard let key = cacheKey(for: avatar) else { return nil }
		return imageCache.object(forKey: key)
	}

	func store(_ avatar: Avatar, image: UIImage) async {
		guard let key = cacheKey(for: avatar) else { return }
		imageCache.setObject(image, forKey: key)
	}
}
