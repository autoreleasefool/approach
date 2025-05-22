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
							Image(systemName: material.systemImage)
								.resizable()
						}
						if let pinFall {
							Image(systemName: pinFall.systemImage)
								.resizable()
						}
						if let mechanism {
							Image(systemName: mechanism.systemImage)
								.resizable()
						}
						if let pinBase {
							Image(systemName: pinBase.systemImage)
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
	public var systemImage: String {
		switch self {
		case .synthetic: "s.circle"
		case .wood: "w.circle"
		}
	}
}

extension Alley.PinFall {
	public var systemImage: String {
		switch self {
		case .freefall: "figure.fall"
		case .strings: "lines.measurement.horizontal"
		}
	}
}

extension Alley.Mechanism {
	public var systemImage: String {
		switch self {
		case .dedicated: "5.circle"
		case .interchangeable: "10.circle"
		}
	}
}

extension Alley.PinBase {
	public var systemImage: String {
		switch self {
		case .black: "capsule.portrait.bottomhalf.filled"
		case .white: "capsule.portrait"
		case .other: "capsule.portrait.righthalf.filled"
		}
	}
}

#if DEBUG
#Preview {
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
#endif
