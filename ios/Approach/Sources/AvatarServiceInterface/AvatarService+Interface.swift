import Dependencies
import Foundation
import ModelsLibrary
import SwiftUI

public struct AvatarService {
	public var render: @Sendable (Avatar.Value) async -> UIImage?
	public var preRender: @Sendable (Avatar.Value) async -> Void

	public init(
		render: @escaping @Sendable (Avatar.Value) async -> UIImage?,
		preRender: @escaping @Sendable (Avatar.Value) async -> Void
	) {
		self.render = render
		self.preRender = preRender
	}
}

extension AvatarService: TestDependencyKey {
	public static var testValue: Self { 
		Self(
			render: { _ in unimplemented("\(Self.self).render") },
			preRender: { _ in unimplemented("\(Self.self).preRender")}
		)
	}
}
