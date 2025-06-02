import Foundation

@dynamicMemberLookup
public enum Loadable<Value> {
	case notLoaded
	case loaded(Value)

	public var value: Value? {
		switch self {
		case .notLoaded: nil
		case let .loaded(value): value
		}
	}

	public subscript<T>(dynamicMember keyPath: KeyPath<Value, T>) -> T? {
		value?[keyPath: keyPath]
	}
}

extension Loadable: Sendable where Value: Sendable {}
extension Loadable: Equatable where Value: Equatable {}
