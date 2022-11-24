import ComposableArchitecture
import SwiftUI

public struct ListContent<
	Element: Identifiable & Equatable,
	ErrorContent: Equatable,
	Content: View,
	NoContent: View,
	ErrorView: View
>: View {
	public typealias State = ListContentState<Element, ErrorContent>

	public let state: State
	public let content: (IdentifiedArrayOf<Element>) -> Content
	public let emptyView: () -> NoContent
	public let errorView: (ErrorContent) -> ErrorView

	public init(
		_ state: State,
		@ViewBuilder content: @escaping (IdentifiedArrayOf<Element>) -> Content,
		@ViewBuilder empty: @escaping () -> NoContent,
		@ViewBuilder error: @escaping (ErrorContent) -> ErrorView
	) {
		self.state = state
		self.content = content
		self.emptyView = empty
		self.errorView = error
	}

	@ViewBuilder public var body: some View {
		switch state {
		case .notLoaded:
			EmptyView()
		case .loading:
			ProgressView()
		case let .loaded(elements):
			if elements.isEmpty {
				emptyView()
			} else {
				List {
					content(elements)
				}
			}
		case let .error(errorContent):
			errorView(errorContent)
		}
	}
}

public enum ListContentState<Element: Identifiable & Equatable, ErrorContent: Equatable>: Equatable {
	case notLoaded
	case loading
	case loaded(IdentifiedArrayOf<Element>)
	case error(ErrorContent)
}
