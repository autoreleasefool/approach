import ComposableArchitecture
import FeatureActionLibrary
import IdentifiedCollections
import SwiftUI
import UniformTypeIdentifiers

public struct Reorderable<Content: View, Item: Identifiable & Equatable>: Reducer {
	public struct State: Equatable {
		public var items: IdentifiedArrayOf<Item>

		public init(items: IdentifiedArrayOf<Item>) {
			self.items = items
		}
	}

	public enum Action: FeatureAction, Equatable {
		public enum ViewAction: Equatable {
			case didMoveItem(from: IndexSet, to: Int)
			case didFinishReordering
		}
		public enum DelegateAction: Equatable {
			case itemDidMove(from: IndexSet, to: Int)
			case didFinishReordering
		}
		public enum InternalAction: Equatable {}

		case view(ViewAction)
		case delegate(DelegateAction)
		case `internal`(InternalAction)
	}

	public init() {}

	public var body: some Reducer<State, Action> {
		Reduce<State, Action> { _, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case let .didMoveItem(from, to):
					return .send(.delegate(.itemDidMove(from: from, to: to)))

				case .didFinishReordering:
					return .send(.delegate(.didFinishReordering))
				}

			case let .internal(internalAction):
				switch internalAction {
				case .never:
					return .none
				}

			case .delegate:
				return .none
			}
		}
	}
}

public struct ReorderableView<Content: View, Item: Identifiable & Equatable>: View {
	let store: StoreOf<Reorderable<Content, Item>>
	let content: (Item) -> Content

	public init(store: StoreOf<Reorderable<Content, Item>>, @ViewBuilder content: @escaping (Item) -> Content) {
		self.store = store
		self.content = content
	}

	@State private var itemBeingDragged: Item?
	@State private var draggedItemHasMoved = false

	public var body: some View {
		WithViewStore(store, observe: { $0 }, send: { .view($0) }, content: { viewStore in
			ForEach(viewStore.items) { item in
				content(item)
				.opacity(
					itemBeingDragged == item && draggedItemHasMoved
					? 0.7
					: 1.0
				)
				.onDrag {
					itemBeingDragged = item
					return NSItemProvider(object: "\(item.id)" as NSString)
				}
				.onDrop(of: [UTType.text], delegate: ReorderingDelegate(
					item: item,
					items: viewStore.items,
					itemBeingDragged: $itemBeingDragged,
					draggedItemHasMoved: $draggedItemHasMoved,
					onMove: { viewStore.send(.didMoveItem(from: $0, to: $1), animation: .easeInOut) },
					onDrop: { viewStore.send(.didFinishReordering) }
				))
			}
		})
	}
}

struct ReorderingDelegate<Item: Identifiable & Equatable>: DropDelegate {
	let item: Item
	let items: IdentifiedArrayOf<Item>
	@Binding var itemBeingDragged: Item?
	@Binding var draggedItemHasMoved: Bool
	var onMove: (IndexSet, Int) -> Void
	var onDrop: () -> Void

	func dropEntered(info: DropInfo) {
		guard item != itemBeingDragged, let itemBeingDragged else { return }
		guard let from = items.firstIndex(of: itemBeingDragged), let to = items.firstIndex(of: item) else { return }

		draggedItemHasMoved = true

		if items[to] != itemBeingDragged {
			onMove(IndexSet(integer: from), to > from ? to + 1 : to)
		}
	}

	func dropUpdated(info: DropInfo) -> DropProposal? {
		DropProposal(operation: .move)
	}

	func performDrop(info: DropInfo) -> Bool {
		draggedItemHasMoved = false
		itemBeingDragged = nil
		onDrop()
		return true
	}
}
