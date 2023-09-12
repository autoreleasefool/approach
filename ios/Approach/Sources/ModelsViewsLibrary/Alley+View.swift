import AssetsLibrary
import ModelsLibrary
import SwiftUI

extension Alley {
	public struct View: SwiftUI.View {
		private static let columns = [GridItem(.fixed(.extraTinyIcon)), GridItem(.fixed(.extraTinyIcon))]

		let name: String
		let locationName: String?
		let material: Alley.Material?
		let pinFall: Alley.PinFall?
		let mechanism: Alley.Mechanism?
		let pinBase: Alley.PinBase?

		public init(
			name: String,
			locationName: String?,
			material: Alley.Material?,
			pinFall: Alley.PinFall?,
			mechanism: Alley.Mechanism?,
			pinBase: Alley.PinBase?
		) {
			self.name = name
			self.locationName = locationName
			self.material = material
			self.pinFall = pinFall
			self.mechanism = mechanism
			self.pinBase = pinBase
		}

		public init(_ alley: Alley.Summary) {
			self.init(
				name: alley.name,
				locationName: alley.location?.subtitle,
				material: alley.material,
				pinFall: alley.pinFall,
				mechanism: alley.mechanism,
				pinBase: alley.pinBase
			)
		}

		public init(_ alley: Alley.List) {
			self.init(
				name: alley.name,
				locationName: alley.location?.subtitle,
				material: alley.material,
				pinFall: alley.pinFall,
				mechanism: alley.mechanism,
				pinBase: alley.pinBase
			)
		}

		public var body: some SwiftUI.View {
			HStack {
				VStack(alignment: .leading) {
					Text(name)
						.font(.headline)

					if let locationName, !locationName.isEmpty {
						Text(locationName)
							.font(.body)
					}
				}

				Spacer()

				LazyVGrid(columns: Self.columns, spacing: 0) {
					Group {
						if let material {
							Image(systemSymbol: material.systemSymbol)
								.resizable()
						}
						if let pinFall {
							Image(systemSymbol: pinFall.systemSymbol)
								.resizable()
						}
						if let mechanism {
							Image(systemSymbol: mechanism.systemSymbol)
								.resizable()
						}
						if let pinBase {
							Image(systemSymbol: pinBase.systemSymbol)
								.resizable()
						}
					}
					.scaledToFit()
					.frame(width: .tinyIcon, height: .tinyIcon)
					.padding(.tinySpacing)
				}
				.fixedSize(horizontal: true, vertical: false)
			}
		}
	}
}

extension Alley.Material {
	public var systemSymbol: SFSymbol {
		switch self {
		case .synthetic: return .sCircle
		case .wood: return .wCircle
		}
	}
}

extension Alley.PinFall {
	public var systemSymbol: SFSymbol {
		switch self {
		case .freefall: return .figureFall
		case .strings: return .linesMeasurementHorizontal
		}
	}
}

extension Alley.Mechanism {
	public var systemSymbol: SFSymbol {
		switch self {
		case .dedicated: return ._5Circle
		case .interchangeable: return ._10Circle
		}
	}
}

extension Alley.PinBase {
	public var systemSymbol: SFSymbol {
		switch self {
		case .black: return .capsulePortraitBottomhalfFilled
		case .white: return .capsulePortrait
		case .other: return .capsulePortraitRighthalfFilled
		}
	}
}

#if DEBUG
struct AlleyViewPreview: PreviewProvider {
	static var previews: some View {
		List {
			Alley.View(
				name: "Skyview Lanes",
				locationName: nil,
				material: nil,
				pinFall: nil,
				mechanism: nil,
				pinBase: nil
			)
			Alley.View(
				name: "Grandview Lanes",
				locationName: "Vancouver, BC",
				material: .synthetic,
				pinFall: .freefall,
				mechanism: .dedicated,
				pinBase: .black
			)
		}
	}
}
#endif
