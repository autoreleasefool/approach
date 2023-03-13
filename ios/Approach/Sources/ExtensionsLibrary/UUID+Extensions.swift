import Foundation

extension UUID {
	public static let placeholder = UUID(uuidString: "00000000-0000-0000-0000-000000000000")!
}

extension UUID: Identifiable {
	public var id: Self { self }
}
